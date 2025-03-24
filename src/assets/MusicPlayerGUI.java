package assets;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {
    //color configuration
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;
    //allows to use file explorer in the app
    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtists;
    private JPanel playbackBtns;
    private JSlider playbackSlider;

    public MusicPlayerGUI() {
        //super( title: "Music Player");
        setSize(430, 600);
        //end process when the app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //launch the app at the center of the screen
        setLocationRelativeTo(null);
        //prevents the app from being resized
        setResizable(false);
        setLayout(null);

        //change the frame color
        getContentPane().setBackground(FRAME_COLOR);
         musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();

        //set a default path for file explorer
        jFileChooser.setCurrentDirectory(new File("src/assets"));

        //filter file chooser to only see .mp3 files
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        addGuiComponents();
    }

    private void addGuiComponents() {
        //add toolbar
        addToolbar();

        //load record image
        JLabel songImage = new JLabel(loadImage("src/assets/MP3 Music Player GUI Image Assets/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        //song title
         songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //song artist
        songArtists = new JLabel("Artist");
        songArtists.setBounds(0, 315, getWidth() - 10, 30);
        songArtists.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtists.setForeground(TEXT_COLOR);
        songArtists.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtists);

        //playback slider
         playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth() / 2 - 300 / 2, 365, 300, 40);
        playbackSlider.setBackground(null);
        add(playbackSlider);

        //playback buttons (i.e. previous, play, next)
          addPlaybackBtns();
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);
        //prevent the toolbar from being moved
        toolBar.setFloatable(false);

        //add dropdown menu
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        //add a song menu
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        //adding the load song item in the songMenu
        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //an integer is returned to let us know what the user did
              int result  =  jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    //song object based on selected file
                    Song song = new Song(selectedFile.getPath());

                    //load song in music player
                    musicPlayer.loadSong(song);

                    //update song title and artist
                    updateSongTitleAndArtist(song);

                    //update playback slider
                    updatePlaybackSlider(song);

                    //toggle on pause button and toggle off play button
                    enablePauseButtonDisablePlayButton();

                }
            }
        });
        songMenu.add(loadSong);
        //playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        JMenuItem createPlaylist = new JMenuItem("Create a Playlist");
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        playlistMenu.add(loadPlaylist);
        menuBar.add(playlistMenu);
        add(toolBar);
    }

    private void addPlaybackBtns(){
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0,435,getWidth()-10,80);
        playbackBtns.setBackground(null);
        JButton previousBtn = new JButton(loadImage("src/assets/MP3 Music Player GUI Image Assets/previous.png"));
        previousBtn.setBorderPainted(false);
        previousBtn.setBackground(null);
        playbackBtns.add(previousBtn);

        //play button
        JButton playBtn = new JButton(loadImage("src/assets/MP3 Music Player GUI Image Assets/play.png"));
        playBtn.setBorderPainted(false);
        playBtn.setBackground(null);
       playBtn.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               enablePauseButtonDisablePlayButton();
               //play or resume song
               musicPlayer.playCurrentSong();
           }
       });

        playbackBtns.add(playBtn);

        //pause button
        JButton pauseButton = new JButton(loadImage("src/assets/MP3 Music Player GUI Image Assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //toggle off pause button and toggle on play button
                enablePlayButtonDisablePauseButton();

                //pause the song
                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton);

        //next button
        JButton nextButton = new JButton(loadImage("src/assets/MP3 Music Player GUI Image Assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    //updates the slider from the music player class
    public void setPlaybackSliderValue(int frame)
    {
      playbackSlider.setValue(frame);

    }

    private void updateSongTitleAndArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songArtists.setText(song.getSongArtist());
    }

    private void updatePlaybackSlider(Song song){
        //update max count for slider
        playbackSlider.setMaximum(song.getMp3file().getFrameCount());

        //create the song legth label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        //biggining will be 00.00
        JLabel labelBeginning = new  JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD,18));
        labelBeginning.setForeground(TEXT_COLOR);

        //end will vary depending on the song
        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD,18));
        labelEnd.setForeground(TEXT_COLOR);


        labelTable.put(0,labelBeginning);
        labelTable.put(song.getMp3file().getFrameCount(),labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    private void enablePauseButtonDisablePlayButton(){
   //retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
         JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        //turn off play button
        playButton.setVisible(false);
        playButton.setEnabled(false);

        //turn on pause button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    private void enablePlayButtonDisablePauseButton(){
        //retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        //turn on play button
        playButton.setVisible(true);
        playButton.setEnabled(true);

        //turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath) {
        try {
            //reads the image file
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
