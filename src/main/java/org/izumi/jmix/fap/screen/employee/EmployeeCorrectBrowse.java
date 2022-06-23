package org.izumi.jmix.fap.screen.employee;

import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.core.Metadata;
import io.jmix.core.SaveContext;
import io.jmix.core.Stores;
import io.jmix.data.StoreAwareLocator;
import io.jmix.ui.action.Action;
import io.jmix.ui.screen.*;
import org.izumi.jmix.fap.entity.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Employee.correct-browse")
@UiDescriptor("employee-correct-browse.xml")
@LookupComponent("employeesTable")
public class EmployeeCorrectBrowse extends StandardLookup<Employee> {
    private static final Logger log = LoggerFactory.getLogger(EmployeeBrowse.class);

    @Autowired
    private DataManager dataManager;
    @Autowired
    private Metadata metadata;
    @Autowired
    private StoreAwareLocator storeAwareLocator;

    @Subscribe("employeesTable.generate")
    public void onEmployeesTableGenerate(Action.ActionPerformedEvent event) {
        final var saveContext = new SaveContext();
        final var initialIndex = count(Employee.class) + 1;
        for (int offset = 0 ; offset < 11; offset++) {
            final var index = initialIndex + offset;

            final var employee = metadata.create(Employee.class);
            employee.setFirstName("First name #" + index);
            employee.setLastName("Last name #" + index);

            saveContext.saving(employee);
        }

        dataManager.save(saveContext);

        getScreenData().loadAll();
    }

    @Subscribe("employeesTable.clear")
    public void onEmployeesTableClear(Action.ActionPerformedEvent event) {
        clear(Employee.class);
    }

    private long count(Class<?> clazz) {
        final var metaClass = metadata.getClass(clazz);
        final var loadContext = new LoadContext<>(metaClass);
        loadContext.setQuery(new LoadContext.Query("SELECT e FROM " + metaClass.getName() + " e"));

        return dataManager.getCount(loadContext);
    }

    private void clear(Class<?> clazz) {
        final var metaClass = metadata.getClass(clazz);

        final var transactionTemplate = storeAwareLocator.getTransactionTemplate(Stores.MAIN);
        transactionTemplate.executeWithoutResult(transaction -> {
            final var entityManager = storeAwareLocator.getEntityManager(Stores.MAIN);
            final var query = String.format("DELETE FROM %s e", metaClass.getName());
            final var updated = entityManager.createQuery(query).executeUpdate();
            log.info("{} entities of class {} were deleted", updated, metaClass.getName());
        });
    }
}