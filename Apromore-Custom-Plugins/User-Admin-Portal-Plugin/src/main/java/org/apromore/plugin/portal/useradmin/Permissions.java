package org.apromore.plugin.portal.useradmin;

/**
 * These permissions must be a subset of the ones in the database.
 */
public enum Permissions {
    VIEW_USERS("dff60714-1d61-4544-8884-0d8b852ba41e"),
    EDIT_USERS("2e884153-feb2-4842-b291-769370c86e44"),
    EDIT_GROUPS("d9ade57c-14c7-4e43-87e5-6a9127380b1b"),
    EDIT_ROLES("ea31a607-212f-447e-8c45-78f1e59b1dde");

    private final String rowGuid;

    Permissions(String newRowGuid) { this.rowGuid = newRowGuid; }

    public String getRowGuid() { return rowGuid; }
}
