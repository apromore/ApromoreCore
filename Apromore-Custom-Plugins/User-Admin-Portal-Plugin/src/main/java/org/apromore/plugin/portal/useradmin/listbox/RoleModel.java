package org.apromore.plugin.portal.useradmin.listbox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.apromore.dao.model.Role;

@Getter
@AllArgsConstructor
public class RoleModel {
    @NonNull
    Role role;
    String label;

    public String getLabel() {
        return label == null ? role.getName() : label;
    }
}
