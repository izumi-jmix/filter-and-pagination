package org.izumi.jmix.fap.screen.employee;

import io.jmix.ui.screen.*;
import org.izumi.jmix.fap.entity.Employee;

@UiController("Employee.edit")
@UiDescriptor("employee-edit.xml")
@EditedEntityContainer("employeeDc")
public class EmployeeEdit extends StandardEditor<Employee> {
}