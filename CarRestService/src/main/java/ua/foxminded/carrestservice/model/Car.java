package ua.foxminded.carrestservice.model;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cars")
public class Car {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "make", referencedColumnName = "id", nullable = false)
    private Manufacturer manufacturer;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @ManyToMany(fetch = FetchType.LAZY,
                cascade = { CascadeType.DETACH, CascadeType.MERGE,
                            CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "cars_categories", joinColumns = @JoinColumn(name = "car_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @Override
    public String toString() {
        String categoryNames = categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));

        return String.format("%s %s %d [%s]", manufacturer.getName(), model, year, categoryNames);
    }
}
