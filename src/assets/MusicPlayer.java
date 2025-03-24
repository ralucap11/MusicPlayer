package assets;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    //it's used to update isPaused more synchronously
    private static final Object playSignal = new Object();

    //for updating the gui in this class
    private MusicPlayerGUI musicPlayerGUI;
    //store song details
    private Song currentSong;

    //use JLayer library to create an AdancedPlayer obj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    //pause boolean flag to indicate wether the player has been paused
    private boolean isPaused;

    //stores the 10 last frames (used for pausing and resuming)
    private int currentFrame;

    //tracks how many milliseconds has passed since playing the song
    private int currentTimeInMilliseconds;


    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    public void loadSong(Song song) {
        currentSong = song;
        if (currentSong != null) {
            playCurrentSong();
        }
    }

    public void pauseSong() {
        if (advancedPlayer != null) {
            //update isPaused flag
            isPaused = true;
            //then we want to stop the player
            stopSong();
        }
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;

        }
    }

    public void playCurrentSong() {
        //in case we press the play button without any song
        if (currentSong == null)
            return;
        try {
            //read mp3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);


            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            //start music
            startMusicThread();

            //start playbak slider thread
            startPlaybackSlider();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    //a thread that will handle playing the music
    private void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isPaused) {
                        synchronized (playSignal) {
                            //update flag
                            isPaused = false;
                            //notify the other thread to continue
                            playSignal.notify();
                        }
                        //resume music frm last frame
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);


                    } else {
                        //play music from the beginning
                        advancedPlayer.play();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //create a thread that will handle updating the slider
    private void startPlaybackSlider() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPaused) {

                    if(isPaused){
                        try{
                            //wait till it gets notified by other thread to continue
                            //makes sure isPaused boolean flag updates to false before continuing
                            synchronized (playSignal) {
                                playSignal.wait();
                            }
                        }catch(Exception e){
                            e.printStackTrace();

                        }
                    }
                    try {
                        //increment current time
                        currentTimeInMilliseconds++;
                       System.out.println(currentTimeInMilliseconds * 2.08);

                        //calculate into frame value ,*2.08 for increased accuracy
                        int calculateFrame = (int) ((double) currentTimeInMilliseconds * 2.08 * currentSong.getFrameRatePerMilliseconds());

                        //update gui
                        musicPlayerGUI.setPlaybackSliderValue(calculateFrame);
                        Thread.sleep(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    public void playbackStarted(PlaybackEvent evt) {
        //this method gets called in the beginning of the song
        System.out.println("Playback Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        //this method gets called when the song is finished
        System.out.println("Playback Finished");


        if (isPaused) {

            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
            //to know when the song has paused

        }

    }
}
