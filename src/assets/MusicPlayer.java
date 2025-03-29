package assets;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {
    //it's used to update isPaused more synchronously
    private static final Object playSignal = new Object();

    //for updating the gui in this class
    private MusicPlayerGUI musicPlayerGUI;
    //store song details
    private Song currentSong;

    public Song getCurrentSong() {
        return currentSong;
    }

    private ArrayList<Song> playlist;
    //keeps track the index we are in the playlist
    private int currentPlaylistIndex;
    //use JLayer library to create an AdancedPlayer obj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    //pause boolean flag to indicate wether the player has been paused
    private boolean isPaused;

    //stores the 10 last frames (used for pausing and resuming)
    private int currentFrame;
    private boolean songFinished;
    private boolean pressedNext, pressedPrev;

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }


    //tracks how many milliseconds has passed since playing the song
    private int currentTimeInMilliseconds;

    public void setCurrentTimeInMilliseconds(int timeInMilliseconds) {
        currentTimeInMilliseconds = timeInMilliseconds;

    }

    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    public void loadSong(Song song) {
        currentSong = song;
        playlist = null;
        //stop the song if possible
        if (!songFinished)
            stopSong();
        playlist = null;
        //stop the song if possible
        if (currentSong != null) {
            //reset frame
            currentFrame = 0;
            //reset current time in milliseconds
            currentTimeInMilliseconds = 0;
            //update gui
            musicPlayerGUI.setPlaybackSliderValue(0);
            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();
        //store the paths from the text file into the playlist array list
        try {
            FileReader fileReader = new FileReader((playlistFile));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String songPath;
            while ((songPath = bufferedReader.readLine()) != null) {
                //create song object based on song path
                Song song = new Song(songPath);
                //add to playlist arraylist
                playlist.add(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (playlist.size() > 0) {
            //reset playback slider
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilliseconds = 0;
            //update the current song o the first song in the playlist
            currentSong = playlist.get(0);
            currentFrame = 0;
            //update gui
            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);

            //start song
            playCurrentSong();
        }
    }

    public void nextSong() {
        // no need to go to the next song if there is no playlist
        if (playlist == null) return;

        // check to see if we have reached the end of the playlist, if so then don't do anything
        if (currentPlaylistIndex + 1 > playlist.size() - 1) return;

        pressedNext = true;

        // stop the song if possible
        if (!songFinished)
            stopSong();

        // increase current playlist index
        currentPlaylistIndex++;

        // update current song
        currentSong = playlist.get(currentPlaylistIndex);

        // reset frame
        currentFrame = 0;

        // reset current time in milli
        currentTimeInMilliseconds = 0;

        // update gui
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        // play the song
        playCurrentSong();
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

    public void prevSong() {
        // no need to go to the next song if there is no playlist
        if (playlist == null) return;

        // check to see if we can go to the previous song
        if (currentPlaylistIndex - 1 < 0) return;

        pressedPrev = true;

        // stop the song if possible
        if (!songFinished)
            stopSong();

        // decrease current playlist index
        currentPlaylistIndex--;

        // update current song
        currentSong = playlist.get(currentPlaylistIndex);

        // reset frame
        currentFrame = 0;

        // reset current time in milli
        currentTimeInMilliseconds = 0;

        // update gui
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        // play the song
        playCurrentSong();
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

            //start playback slider thread
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

                if (isPaused) {
                    try {
                        //wait till it gets notified by the other thread to continue
                        //makes sure isPaused boolean flag updates to false before continuing
                        synchronized (playSignal) {
                            playSignal.wait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                while (!isPaused && !songFinished && !pressedNext && !pressedPrev) {
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
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        //this method gets called when the song is finished
        System.out.println("Playback Finished");

        if (isPaused) {

            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
            //to know when the song has paused
        } else {
            //if the user pressed next or prev we don't need to execute the rest of the code
            if (pressedNext || pressedPrev) return;

            //when the song ends
            songFinished = true;
            if (playlist == null) {
                //update gui
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            } else {
                //last song in the playlist
                if (currentPlaylistIndex == playlist.size() - 1) {
                    //update gui
                    musicPlayerGUI.enablePlayButtonDisablePauseButton();
                } else {
                    //go to the next song in the playlist
                    nextSong();
                }
            }
        }


    }
}
