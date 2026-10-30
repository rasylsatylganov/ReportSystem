package kg.home.demo.service;

import jakarta.persistence.Entity;
import kg.home.demo.controller.DrEnterpriseController;
import kg.home.demo.controller.DrPeriodController;
import kg.home.demo.controller.DrReferenceController;
import kg.home.demo.controller.DrReferenceItemController;
import kg.home.demo.controller.DrTypeEnterpriseController;
import kg.home.demo.controller.DrTypePeriodController;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ModelBoundaryTest {

    @ParameterizedTest
    @MethodSource("serviceTypes")
    void serviceContractsContainNoEntities(Class<?> serviceType) {
        this.assertModelBoundary(serviceType);
    }

    @ParameterizedTest
    @MethodSource("controllerTypes")
    void controllerContractsContainNoEntities(Class<?> controllerType) {
        this.assertModelBoundary(controllerType);
    }

    static List<Class<?>> serviceTypes() {
        return List.of(DrEnterpriseService.class, DrPeriodService.class,
                DrReferenceService.class, DrReferenceItemService.class,
                DrTypeEnterpriseService.class, DrTypeFieldService.class,
                DrTypePeriodService.class, DrTypePositionService.class,
                MdReportInService.class, MdReportInReferenceService.class,
                MdReportInReferenceItemService.class, TbReferenceItemService.class,
                TbReferenceItemValueService.class);
    }

    static List<Class<?>> controllerTypes() {
        return List.of(DrEnterpriseController.class, DrPeriodController.class,
                DrReferenceController.class, DrReferenceItemController.class,
                DrTypeEnterpriseController.class, DrTypePeriodController.class);
    }

    private void assertModelBoundary(Class<?> boundaryType) {
        for (Method method : boundaryType.getDeclaredMethods()) {
            this.assertNoEntities(method.getGenericReturnType(), new HashSet<>());
            for (Type parameterType : method.getGenericParameterTypes()) {
                this.assertNoEntities(parameterType, new HashSet<>());
            }
        }
    }

    private void assertNoEntities(Type type, Set<Type> visited) {
        if (!visited.add(type)) {
            return;
        }
        if (type instanceof ParameterizedType parameterizedType) {
            this.assertNoEntities(parameterizedType.getRawType(), visited);
            for (Type argument : parameterizedType.getActualTypeArguments()) {
                this.assertNoEntities(argument, visited);
            }
        }
        if (type instanceof Class<?> declaredType) {
            assertFalse(declaredType.isAnnotationPresent(Entity.class),
                    () -> "JPA entity in public contract: " + declaredType.getName());
            if (declaredType.isRecord()) {
                for (RecordComponent component : declaredType.getRecordComponents()) {
                    this.assertNoEntities(component.getGenericType(), visited);
                }
            }
        }
    }
}
