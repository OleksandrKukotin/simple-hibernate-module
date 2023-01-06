import annotations.ColumnMapping;
import annotations.EntityMapping;
import annotations.IdMapping;

@EntityMapping(tableName = "Employees")
public class Employee {

    @IdMapping
    private int id;

    @ColumnMapping(columnName = "name", columnType = "text")
    private String name;

    @ColumnMapping(columnName = "position", columnType = "text")
    private String position;

    @ColumnMapping(columnName = "salary", columnType = "int")
    private int salary;

    public Employee(int id, String name, String position, int salary) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", position='" + position + '\'' +
            ", salary=" + salary +
            '}';
    }
}
