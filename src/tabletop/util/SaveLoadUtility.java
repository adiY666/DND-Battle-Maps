package tabletop.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;

import tabletop.state.DataState;
import tabletop.model.TokenModel;

/**
 * Handles saving and loading the tabletop session to a file.
 *
 * @author Adi
 */
public class SaveLoadUtility {

    public static boolean saveSession(DataState dataState, File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(dataState);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static DataState loadSession(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            DataState dataState = (DataState) in.readObject();

            // Re-load images for tokens because BufferedImage cannot be serialized
            if (dataState.getActiveTokens() != null) {
                for (TokenModel token : dataState.getActiveTokens().values()) {
                    // Fixed: using getImageFilepath() to match the model
                    if (token.getImageFilepath() != null) {
                        try {
                            token.setOriginalImage(ImageIO.read(new File(token.getImageFilepath())));
                        } catch (Exception ex) {
                            System.out.println("Warning: Could not reload image for " + token.getDisplayName());
                        }
                    }
                }
            }
            return dataState;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
