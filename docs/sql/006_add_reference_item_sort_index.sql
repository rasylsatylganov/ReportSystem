-- Run in the application's Oracle schema before starting the updated backend.
-- Stop all backend instances while applying this script.
-- Rename ORDER_SORT; preserve relative order (NULLS LAST, ID breaks ties).
-- Normalize only if null or duplicate indices prevent reliable swaps.
-- Oracle DDL commits automatically; the script is not one atomic transaction.
SET SERVEROUTPUT ON

DECLARE
    v_count PLS_INTEGER;
    v_old_count PLS_INTEGER;
    v_nulls NUMBER;
    v_duplicates NUMBER;
    v_nullable VARCHAR2(1);
    v_max NUMBER;
    v_next NUMBER;
    v_step NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM USER_TABLES WHERE TABLE_NAME = 'DR_REFERENCE_ITEM';

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Table DR_REFERENCE_ITEM not found in current schema');
    END IF;

    SELECT COUNT(*) INTO v_count
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = 'DR_REFERENCE_ITEM' AND COLUMN_NAME = 'SORT_INDEX';

    SELECT COUNT(*) INTO v_old_count
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = 'DR_REFERENCE_ITEM' AND COLUMN_NAME = 'ORDER_SORT';

    IF v_count > 0 AND v_old_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Both ORDER_SORT and SORT_INDEX exist; reconcile columns before migration');
    ELSIF v_count = 0 AND v_old_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Neither ORDER_SORT nor SORT_INDEX exists');
    ELSIF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE DR_REFERENCE_ITEM RENAME COLUMN ORDER_SORT TO SORT_INDEX';
    END IF;

    EXECUTE IMMEDIATE 'ALTER TABLE DR_REFERENCE_ITEM MODIFY (SORT_INDEX NUMBER(19,0))';

    EXECUTE IMMEDIATE 'SELECT COUNT(*) - COUNT(SORT_INDEX), COUNT(SORT_INDEX) - COUNT(DISTINCT SORT_INDEX) ' ||
        'FROM DR_REFERENCE_ITEM' INTO v_nulls, v_duplicates;
    IF v_nulls > 0 OR v_duplicates > 0 THEN
        EXECUTE IMMEDIATE 'MERGE INTO DR_REFERENCE_ITEM target USING (' ||
            'SELECT ID, ROW_NUMBER() OVER (ORDER BY SORT_INDEX NULLS LAST, ID) AS NEW_SORT_INDEX ' ||
            'FROM DR_REFERENCE_ITEM) source ON (target.ID = source.ID) ' ||
            'WHEN MATCHED THEN UPDATE SET target.SORT_INDEX = source.NEW_SORT_INDEX';
    END IF;

    SELECT NULLABLE INTO v_nullable
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = 'DR_REFERENCE_ITEM' AND COLUMN_NAME = 'SORT_INDEX';

    IF v_nullable = 'Y' THEN
        EXECUTE IMMEDIATE 'ALTER TABLE DR_REFERENCE_ITEM MODIFY (SORT_INDEX NOT NULL)';
    END IF;

    EXECUTE IMMEDIATE 'SELECT GREATEST(NVL(MAX(SORT_INDEX), 0), 0) FROM DR_REFERENCE_ITEM' INTO v_max;
    SELECT COUNT(*) INTO v_count
    FROM USER_SEQUENCES WHERE SEQUENCE_NAME = 'DR_REFERENCE_ITEM_SORT_SEQ';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE SEQUENCE DR_REFERENCE_ITEM_SORT_SEQ START WITH ' ||
            TO_CHAR(v_max + 1, 'TM9') || ' INCREMENT BY 1 NOCACHE NOCYCLE';
    ELSE
        -- On a rerun, advance a lagging sequence without ever resetting it backwards.
        EXECUTE IMMEDIATE 'ALTER SEQUENCE DR_REFERENCE_ITEM_SORT_SEQ INCREMENT BY 1 NOCYCLE';
        EXECUTE IMMEDIATE 'SELECT DR_REFERENCE_ITEM_SORT_SEQ.NEXTVAL FROM dual' INTO v_next;
        IF v_next <= v_max THEN
            v_step := v_max + 1 - v_next;
            EXECUTE IMMEDIATE 'ALTER SEQUENCE DR_REFERENCE_ITEM_SORT_SEQ INCREMENT BY ' || TO_CHAR(v_step, 'TM9');
            EXECUTE IMMEDIATE 'SELECT DR_REFERENCE_ITEM_SORT_SEQ.NEXTVAL FROM dual' INTO v_next;
            EXECUTE IMMEDIATE 'ALTER SEQUENCE DR_REFERENCE_ITEM_SORT_SEQ INCREMENT BY 1';
        END IF;
    END IF;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('DR_REFERENCE_ITEM: SORT_INDEX and sort sequence are ready');
END;
/
