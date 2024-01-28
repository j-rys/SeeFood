import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A program that uses the Google Cloud Vision API to determine
 * if a photo specified by the user contains a hot dog or does not.
 */
public class SeeFood {

    /**
     * Prompts the user to select a file,
     * by displaying a JFileChooser that allows the user to select JPEGs and PNGs.
     *
     * NOTE: You may NOT change the method name, the return type,
     *       or the parameter list.
     *
     * @return the (absolute path) filename selected by the user
     *         or the empty string if the user clicked cancel.
     */
    public static String getFilename() {
        JFrame frame = new JFrame("JFileChooser");
        JFileChooser chooser = new JFileChooser("src/images");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG Images", "jpg", "jpeg", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null,
                    "No file selected. Exiting.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        return chooser.getSelectedFile().getAbsolutePath();
    } // end getFilename

    /**
     * Displays the specified image overlaid with a message that says
     * either "Hot Dog" or "Not Hot Dog" depending on if a hot dog
     * is detected by Google Cloud Vision.
     *
     * NOTE: You may NOT change the method name, the return type,
     *       the parameter list, or the throws clause.
     *
     * NOTE: the exceptions listed in the throws clause are there because
     *       you do NOT handle the exceptions inside this method.
     *       You WILL handle the exceptions in main.
     *
     * @param filename the name of the image file.
     *
     * @throws IOException if an I/O error occurs when calling GoogleCloudVision.detectImageLabels
     * @throws InterruptedException here because GoogleCloudVision.detectImageLabels can throw this exception.
     */
    public static void labelImage(String filename) throws IOException, InterruptedException {

        GoogleCloudVision gcV = new GoogleCloudVision();
        System.out.println(gcV.detectImageLabels(filename));
        ArrayList <ObjectLabel> list = gcV.detectImageLabels(filename);


        JLabel imgLbl = new JLabel(new ImageIcon(filename));
        imgLbl.setLayout(new BorderLayout());
        JFrame frame = new JFrame("SeeFood");

        String topText = "Hot Dog";
        String bottomText = "Not Hot Dog";
        System.out.println(list.get(5).getLabel());
        for (int i = 0; i < list.size(); i++){
        if (list.get(i).getLabel().contains("Hot dog")) {

            JLabel topLbl = new JLabel(topText);
            topLbl.setHorizontalAlignment(SwingConstants.CENTER);
            topLbl.setForeground(Color.WHITE);
            topLbl.setBackground(Color.GREEN);
            topLbl.setOpaque(true);
            Font biggerFont = topLbl.getFont().deriveFont(Font.BOLD, 36f);
            topLbl.setFont(biggerFont);
            imgLbl.add(topLbl, BorderLayout.NORTH);// end top label
            i = list.size() - 1;
        }
        else if (i == list.size() - 1 ) {

            JLabel bottomLbl = new JLabel(bottomText);
            bottomLbl.setHorizontalAlignment(SwingConstants.CENTER);
            bottomLbl.setForeground(Color.WHITE);
            bottomLbl.setBackground(Color.RED);
            bottomLbl.setOpaque(true);
            Font biggerFont = bottomLbl.getFont().deriveFont(Font.BOLD, 36f);
            bottomLbl.setFont(biggerFont);
            imgLbl.add(bottomLbl, BorderLayout.SOUTH);// end bottom label
        }
        }
        frame.add(imgLbl);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    } // end labelImage

    /**
     * The entry point of the program.
     *
     * NOTE: You may NOT change the method name, the return type,
     *       the parameter list, or the throws clause.
     *
     * NOTE: You are NOT allowed to add exceptions to the throws clause of main,
     *       You MUST handle any the checked exceptions thrown by labelImage.
     *       Your exception handling code must graphically (using JOptionPane)
     *       show an error message for the user.
     */
    public static void main(String[] args)  {

        String file = getFilename();


        try {
            labelImage(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } // end main

} // end class SeeFood
