package assets;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


public class MusicPlaylistDialog extends JDialog {
    private MusicPlayerGUI musicPlayerGUI;

    //store all  the paths to be written to a text file (when we load a playlist)
    private ArrayList<String> songPaths;

    public MusicPlaylistDialog(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
        songPaths = new ArrayList<>();
        //configure dialog
        setTitle("Create a Playlist");
        setSize(400, 400);
        setResizable(false);
        getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true); //the dialog has to be closed to give focus
        setLocationRelativeTo(musicPlayerGUI);

        addDialogComponents();
    }

    private void addDialogComponents() {
        //container to hold each song path
        JPanel songContainer = new JPanel();
        songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
        songContainer.setBounds((int) (getWidth() * 0.025), 10, (int) (getWidth() * 0.90), (int) (getHeight() * 0.75));
        add(songContainer);

        //add song button
        JButton addSongButton = new JButton("Add");
        addSongButton.setBounds(60, (int) (getHeight() * 0.80), 100, 25);
        addSongButton.setFont(new Font("Dialog", Font.BOLD, 14));
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //open file explorer
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
                jFileChooser.setCurrentDirectory(new File("src/assets"));
                int result = jFileChooser.showOpenDialog(MusicPlaylistDialog.this);

                File selectedFile = jFileChooser.getSelectedFile();
                if (result == jFileChooser.APPROVE_OPTION && selectedFile != null) {
                    JLabel filePathLabel = new JLabel(selectedFile.getPath());
                    filePathLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                    filePathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    //add to the list
                    songPaths.add(filePathLabel.getText());

                    //add to container
                    songContainer.add(filePathLabel);

                    //refreshes dialog to show newly added
                    songContainer.revalidate();
                }
            }
        });
        add(addSongButton);

        //save playlist button
        JButton savePLylistButton = new JButton("Save");
        savePLylistButton.setBounds(215, (int) (getHeight() * 0.80), 100, 25);
        savePLylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
        savePLylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setCurrentDirectory(new File("src/assets"));
                    int result = jFileChooser.showSaveDialog(MusicPlaylistDialog.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        //getSelectedFile() to get reference to the file that we are about to save
                        File selectedFile = jFileChooser.getSelectedFile();

                        //convert to .txt file if not done so already
                        //this will check to see if the file does not have the ".txt" file extension
                        if (selectedFile.getName().length() < 4 ||
                                !selectedFile.getName().substring(selectedFile.getName().length() - 4).equalsIgnoreCase(".txt")) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                        }
                        //create the new file at the destined directory
                        selectedFile.createNewFile();

                        //write all the song paths into this file
                        FileWriter fileWriter = new FileWriter(selectedFile);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        //iterate through our song paths list and write each string into the file
                        //each song will be written in their own row
                        for (String songPath : songPaths) {
                            bufferedWriter.write(songPath + "\n");
                        }
                        bufferedWriter.close();

                        //display dialog
                        JOptionPane.showMessageDialog(MusicPlaylistDialog.this, "The playlist was created succesfully!");

                        //close this dialog
                        MusicPlaylistDialog.this.dispose();
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        add(savePLylistButton);

    }
}
