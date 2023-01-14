import annotations.ColumnMapping;
import annotations.EntityMapping;
import annotations.IdMapping;

@EntityMapping(name = "Employees")
public class Employee {

    @IdMapping
    private Integer id;

    @ColumnMapping
    private String name;

    @ColumnMapping
    private String position;

    @ColumnMapping
    private Integer salary;

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
