import annotations.ColumnMapping;
import annotations.EntityMapping;
import annotations.IdMapping;

@EntityMapping(tableName = "Employees")
public class Employee {

    @IdMapping
    private int id;

    @ColumnMapping
    private String name;

    @ColumnMapping
    private String position;

    @ColumnMapping
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
