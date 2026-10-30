-- Run in the application's Oracle schema before starting the updated backend.
-- Stop all backend instances while applying this script.
-- Existing non-null SORT_INDEX values are preserved; missing values receive ID.
-- Oracle DDL commits automatically; the script is not one atomic transaction.
SET SERVEROUTPUT ON

DECLARE
    v_count PLS_INTEGER;
    v_nullable VARCHAR2(1);
    v_max NUMBER;
    v_next NUMBER;
    v_step NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM USER_TABLES WHERE TABLE_NAME = 'DR_PERIOD';

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Table DR_PERIOD not found in current schema');
    END IF;

    SELECT COUNT(*) INTO v_count
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = 'DR_PERIOD' AND COLUMN_NAME = 'SORT_INDEX';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE DR_PERIOD ADD (SORT_INDEX NUMBER(19,0))';
    END IF;

    -- Dynamic SQL also works when the column is first added by this block.
    EXECUTE IMMEDIATE 'UPDATE DR_PERIOD SET SORT_INDEX = ID WHERE SORT_INDEX IS NULL';

    SELECT NULLABLE INTO v_nullable
    FROM USER_TAB_COLUMNS
    WHERE TABLE_NAME = 'DR_PERIOD' AND COLUMN_NAME = 'SORT_INDEX';

    IF v_nullable = 'Y' THEN
        EXECUTE IMMEDIATE 'ALTER TABLE DR_PERIOD MODIFY (SORT_INDEX NOT NULL)';
    END IF;

    EXECUTE IMMEDIATE 'SELECT GREATEST(NVL(MAX(SORT_INDEX), 0), 0) FROM DR_PERIOD' INTO v_max;
    SELECT COUNT(*) INTO v_count
    FROM USER_SEQUENCES WHERE SEQUENCE_NAME = 'DR_PERIOD_SORT_SEQ';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE SEQUENCE DR_PERIOD_SORT_SEQ START WITH ' ||
            TO_CHAR(v_max + 1, 'TM9') || ' INCREMENT BY 1 NOCACHE NOCYCLE';
    ELSE
        -- On a rerun, advance a lagging sequence without ever resetting it backwards.
        EXECUTE IMMEDIATE 'ALTER SEQUENCE DR_PERIOD_SORT_SEQ INCREMENT BY 1 NOCYCLE';
        EXECUTE IMMEDIATE 'SELECT DR_PERIOD_SORT_SEQ.NEXTVAL FROM dual' INTO v_next;
        IF v_next <= v_max THEN
            v_step := v_max + 1 - v_next;
            EXECUTE IMMEDIATE 'ALTER SEQUENCE DR_PERIOD_SORT_SEQ INCREMENT BY ' || TO_CHAR(v_step, 'TM9');
            EXECUTE IMMEDIATE 'SELECT DR_PERIOD_SORT_SEQ.NEXTVAL FROM dual' INTO v_next;
            EXECUTE IMMEDIATE 'ALTER SEQUENCE DR_PERIOD_SORT_SEQ INCREMENT BY 1';
        END IF;
    END IF;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('DR_PERIOD: SORT_INDEX and sort sequence are ready');
END;
/
