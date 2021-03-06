package mvc.spring.restmvc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mvc.spring.restmvc.model.enums.UserProfile;

import javax.persistence.*;
import java.util.Set;

@JsonPropertyOrder({"id", "name", "permissions"})
@Data
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UserProfile role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "perm_by_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @JsonCreator
    @java.beans.ConstructorProperties({"name", "permissions"})
    public Role(UserProfile userProfile, Set<Permission> permissions) {
        this.role = userProfile;
        this.permissions = permissions;
    }

}
