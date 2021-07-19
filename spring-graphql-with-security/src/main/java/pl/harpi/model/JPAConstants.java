package pl.harpi.model;

public final class JPAConstants {
    private JPAConstants() {
    }

    public static final int TYPE_LENGTH = 120;
    public static final int NAME_LENGTH = 240;

    public static final int USERNAME_LENGTH = 240;
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final String USERNAME_MESSAGE = "Minimum username length: " + USERNAME_MIN_LENGTH + " characters";

    public static final int PASSWORD_LENGTH = 240;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final String PASSWORD_MESSAGE = "Minimum password length: " + PASSWORD_MIN_LENGTH + " characters";

    public static final int COUNTRY_CODE_LENGTH = 3;
    public static final int DISCRIMINATOR_LENGTH = 240;

    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_COUNTRY_CODE = "COUNTRY_CODE";
    public static final String COLUMN_VISIBILITY = "VISIBILITY";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_LOCKED = "LOCKED";

    public static final String COLUMN_OBJECT_DISCRIMINATOR = "OBJECT_TYPE";

    public static final String COLUMN_JOIN_OBJECT_ID = "OBJECT_ID";

    public static final String TABLE_USER = "AD_USER";
    public static final String TABLE_USER_ROLE = "AD_USER_ROLE";

    public static final String TABLE_OBJECT = "PR_OBJECT";
    public static final String TABLE_OBJECT_ML = "PR_OBJECT_ML";
    public static final String TABLE_WORKSPACE = "PR_WORKSPACE";

    public static final String TABLE_OBJECT_CONSTRAINT_TYPE_NAME = "PR_OBJECT_TYPE_NAME";

    public static final String OBJECT_WORKSPACE_DISCRIMINATOR = "WORKSPACE";
}
