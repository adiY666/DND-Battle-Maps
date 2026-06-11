import java.io.*;

/**
 * Represents a file saving utility instance.
 * <p></p>
 * Handles state persistence for this FileUtility.
 *
 * @author Adi
 */
class FileUtility {

    public FileUtility() {
        super();
    }

    /**
     * Saves the state to a file.
     *
     * @param dataState  the state object
     * @param targetFile the destination file
     */
    public void saveState(DataState dataState, File targetFile) {
        try (FileOutputStream fileStream = new FileOutputStream(targetFile)) {
            try (ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
                objectStream.writeObject(dataState);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Loads the state from a file.
     *
     * @param targetFile the origin file
     * @return the loaded state object
     */
    public DataState loadState(File targetFile) {
        try (FileInputStream fileStream = new FileInputStream(targetFile)) {
            try (ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {
                return (DataState) objectStream.readObject();
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
