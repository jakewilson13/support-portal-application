package com.getarrays.supportportalapplication.enumeration;

import static com.getarrays.supportportalapplication.constant.Authorities.*;

//defining all of the roles that we are giving to users, roles coming from our Authorities class imported statically
public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private String[] authorities;

    //takes in any number of authorities
    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
