package kg.home.demo.service;

import kg.home.demo.exception.TemplateConflictException;
import kg.home.demo.exception.TemplateValidationException;
import kg.home.demo.model.*;
import kg.home.demo.repository.TbReferenceItemRepository;
import kg.home.demo.repository.TbReferenceItemValueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=none")
class ReportTemplateIntegrationTest {
    @Autowired private ReportTemplateService service;
    @Autowired private TbReferenceItemRepository dataReferences;
    @Autowired private TbReferenceItemValueRepository dataValues;

    static ReportTemplateRequest request(ReportTemplate template, boolean create, String code) {
        var report = template.report();
        return new ReportTemplateRequest(new MdReportInRequest(code, report.nameReportIn(),
                new IdReference(report.typePeriod().id()), new IdReference(report.typeEnterprise().id()),
                report.indexRowScanBegin(), report.indexRowScanEnd()),
                template.references().stream().map(ref -> new ReportTemplateRequest.ReferenceInput(
                        create ? null : ref.id(), ref.reference().id(), ref.typePosition().id(),
                        ref.indexNum(), ref.orderSort())).toList(),
                template.fields().stream().map(field -> new ReportTemplateRequest.FieldInput(
                        create ? null : field.id(), field.referenceItem().id(), field.typeFieldValue().id(),
                        field.indexColumn(), field.orderSort(), field.isRequired())).toList(),
                create ? null : template.revision());
    }

    @Test
    @Transactional(readOnly = true)
    void opensAndValidatesAllFourDocumentedExamplesOnOracle() {
        var page = service.findPage(PageRequest.of(0, 20, Sort.by("id")));
        assertTrue(page.totalElements() >= 4);
        for (long id = 1; id <= 4; id++) {
            var template = service.get(id);
            assertFalse(template.references().isEmpty());
            assertFalse(template.fields().isEmpty());
            service.validate(request(template, false, template.report().codeReportIn()));
            assertEquals(template.revision(), service.get(id).revision());
        }
        assertEquals(2, service.lookups().positions().size());
        assertTrue(service.lookups().fieldTypes().size() >= 3);
    }

    @Test
    @Transactional
    void createsAndUpdatesWholeTemplatePreservingChildIdsCheckboxAndExcelCoordinates() {
        long valuesBefore = dataValues.count();
        long referencesBefore = dataReferences.count();
        String code = "TEST_" + UUID.randomUUID().toString().replace("-", "");
        var created = service.create(request(service.get(4L), true, code));
        var edit = request(created, false, code);
        var first = edit.fields().getFirst();
        var changed = new ArrayList<>(edit.fields());
        changed.set(0, new ReportTemplateRequest.FieldInput(first.id(), first.referenceItemId(),
                first.typeFieldValueId(), first.indexColumn(), 999L, 0));
        var updated = service.update(created.report().id(), new ReportTemplateRequest(edit.report(),
                edit.references(), changed, edit.revision()));
        assertEquals(created.references().stream().map(MdReportInReference::id).toList(),
                updated.references().stream().map(MdReportInReference::id).toList());
        var updatedField = updated.fields().stream().filter(field -> field.id().equals(first.id())).findFirst().orElseThrow();
        assertEquals(0, updatedField.isRequired());
        assertEquals(999L, updatedField.orderSort());
        assertEquals(first.indexColumn(), updatedField.indexColumn());
        assertNotEquals(created.revision(), updated.revision());
        assertThrows(TemplateConflictException.class, () -> service.update(created.report().id(), edit));
        assertEquals(valuesBefore, dataValues.count());
        assertEquals(referencesBefore, dataReferences.count());
        service.delete(created.report().id());
    }

    @Test
    @Transactional(readOnly = true)
    void rejectsDuplicateColumnsAndFieldsOutsideHorizontalReferences() {
        var template = service.get(4L);
        var edit = request(template, false, template.report().codeReportIn());
        var changed = new ArrayList<>(edit.fields());
        var first = changed.getFirst();
        changed.set(0, new ReportTemplateRequest.FieldInput(first.id(), first.referenceItemId(), first.typeFieldValueId(),
                1L, first.orderSort(), first.isRequired()));
        assertThrows(TemplateValidationException.class, () -> service.validate(new ReportTemplateRequest(
                edit.report(), edit.references(), changed, edit.revision())));
        var refsWithoutHeader = edit.references().stream().filter(ref -> ref.typePositionId().equals(2L)).toList();
        assertThrows(TemplateValidationException.class, () -> service.validate(new ReportTemplateRequest(
                edit.report(), refsWithoutHeader, edit.fields(), edit.revision())));
    }

    @Test
    void rejectsRemovalOfUsedMetadataWithoutChangingStoredTemplate() {
        var template = service.get(4L);
        var edit = request(template, false, template.report().codeReportIn());
        assertThrows(TemplateConflictException.class, () -> service.update(4L, new ReportTemplateRequest(
                edit.report(), edit.references(), edit.fields().subList(1, edit.fields().size()), edit.revision())));
        assertThrows(TemplateConflictException.class, () -> service.delete(4L));
        assertEquals(template.revision(), service.get(4L).revision());
    }

    @Test
    void rollsBackChildChangesWhenReportCodeConflictsAtFinalFlush() {
        var original = service.get(2L);
        var edit = request(original, false, service.get(1L).report().codeReportIn());
        var fields = new ArrayList<>(edit.fields());
        var first = fields.getFirst();
        fields.set(0, new ReportTemplateRequest.FieldInput(first.id(), first.referenceItemId(), first.typeFieldValueId(),
                first.indexColumn(), first.orderSort(), first.isRequired() == 1 ? 0 : 1));
        assertThrows(DataIntegrityViolationException.class, () -> service.update(2L,
                new ReportTemplateRequest(edit.report(), edit.references(), fields, edit.revision())));
        assertEquals(original.revision(), service.get(2L).revision());
    }
}
