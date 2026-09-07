package ca.ucalgary.seng300;

import javafx.scene.image.Image;

/**
 * A class that assigns indices to images on the client.
 * Adding new images:
 * - Give the image a name corresponding to its index number starting at 1. (No skipping indices)
 * - Make sure the file is in .png format
 * - Add it under resources/pfps
 * - Update the PFP_COUNT variable to match
 */
public class PFPManager {
    /**
     * A constant for the amount of profile images within the pfps folder in resources.
     * Change this whenever you add a new image
     */
    private static final int PFP_COUNT = 3;

    /**
     * Gets the count of profile images stored in the files
     * @return The profile picture count
     */
    public static int getPFPCount() {
        return PFP_COUNT;
    }

    /**
     * Gets the profile Image corresponding to the user
     * @param user
     * @return
     */
    public static Image getProfilePicture(User user) {
        return getProfilePicture(user.getProfilePicture());
    }

    /**
     * Returns the Image corresponding to the pfp index
     * @param pfpIndex
     * @return
     */
    public static Image getProfilePicture(int pfpIndex) {
        // Throw exception when the profile picture index exceeds the profile picture count
        if (pfpIndex < 1 || pfpIndex > PFP_COUNT) throw new IllegalArgumentException("Profile picture index out of bounds!");
        // Format the string
        String path = String.format("/pfps/%d.png", pfpIndex);
        // Get image from the resource folder
        Image image = new Image(PFPManager.class.getResource(path).toExternalForm());
        return image;
    }
}
