package gfx;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound
{//SOUNDS
	public static String[] songs = {"Graal - Background1.wav",
									"Graal - Background2.wav",
									"Graal - Intro.wav",
									"Graal - Quest.wav",
									"Graal - Story.wav"};
	
	public static String[] soundEffects = {"swordSlash.wav"};
	
	public static Clip music;
	public static Clip sfx;
	public static AudioInputStream sfxStream; //where the sfx is played from (what sfx is playing)
	public static AudioInputStream musicStream; //where the music is played from (Aka what song is playing)

	public static String currentSongName;
	public static String currentSongFileName;
	public static float musicVolume = 50;
	public static float sfxVolume = 50;
	private static float oldSfxVolume;
	public static float musicVolumeChange = 0;
	public static float sfxVolumeChange = 0;
	public static boolean musicMuted = false;
	public static boolean sfxMuted = false;
	
	public static void init()
	{

	}
	
	public static void setSfxVolume(float vol)
	{
	    Sound.sfxVolumeChange = vol;
	    Sound.sfxVolume =  50 + sfxVolumeChange;
	    
		if(Sound.sfxVolumeChange < -50)
			Sound.sfxVolumeChange = -50;
		if(Sound.sfxVolumeChange > 0)
			Sound.sfxVolumeChange = 0;
		if(Sound.sfxVolume < 0)
			Sound.sfxVolume = 0;
		if(Sound.sfxVolume > 50)
			Sound.sfxVolume = 50;
	    if(Sound.sfxMuted)
	    	Sound.sfxVolumeChange = -80;
	}
	
	public static void setMusicVolume(float vol)
	{
	    Sound.musicVolumeChange = vol;
	    Sound.musicVolume = 50 + musicVolumeChange;
	    
		if(Sound.musicVolumeChange < -50)
			Sound.musicVolumeChange = -50;
		if(Sound.musicVolumeChange > 0)
			Sound.musicVolumeChange = 0;
		if(Sound.musicVolume < 0)
			Sound.musicVolume = 0;
		if(Sound.musicVolume > 50)
			Sound.musicVolume = 50;
		
	    //need this since it's looping continuously 
		FloatControl gainControl = (FloatControl) Sound.music.getControl(FloatControl.Type.MASTER_GAIN);
	    gainControl.setValue(Sound.musicVolumeChange); 
	}
	
	public static void muteSfx()
	{
		//if it's not muted, mute it.
		if(!Sound.sfxMuted)
		{
			Sound.sfxMuted = true;
			Sound.oldSfxVolume = Sound.sfxVolumeChange;
			Sound.setSfxVolume(-80);
		}
		else //if it is muted, then unmute it
		{
			Sound.sfxMuted = false;
			Sound.setSfxVolume(Sound.oldSfxVolume);
		}
	}
	
	public static void muteMusic()
	{
		FloatControl gainControl = (FloatControl) Sound.music.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-80); 
		//if it's not muted, mute it.
		if(!Sound.musicMuted)
		{
			Sound.musicMuted = true;
			Sound.musicVolume = 0;
			gainControl.setValue(-80); 
		}
		else //if it is muted, then unmute it
		{
			Sound.musicMuted = false;
			Sound.musicVolume = 50 + Sound.musicVolumeChange;
			gainControl.setValue(Sound.musicVolumeChange); 
		}
	}
	
	public static void playSfx(String url)
	{
		try
		{
			sfx = AudioSystem.getClip();
			sfxStream = AudioSystem.getAudioInputStream(new File("src/resources/sound/soundeffects/"+url));
			sfx.open(sfxStream);
			FloatControl gainControl = (FloatControl) Sound.sfx.getControl(FloatControl.Type.MASTER_GAIN);
		    gainControl.setValue(Sound.sfxVolumeChange); 
		    sfx.start(); 
	        if(Sound.sfxMuted)
	        {
	        	muteSfx();
	        }
		}
		catch(Exception e)
		{
	         System.err.println(e.getMessage());
		}
	}
	
	public static void playMusic(String url)
	{
		try
		{
			music = AudioSystem.getClip();
			musicStream = AudioSystem.getAudioInputStream(new File("src/resources/sound/music/"+url));
			music.open(musicStream);
			music.loop(Clip.LOOP_CONTINUOUSLY);
	        music.start(); 
	        Sound.setMusicVolume(Sound.musicVolumeChange);
	        fixMusicVolume();
	        //String songName = url.substring(0, url.length()-4);
	        Sound.currentSongFileName = url;
	        Sound.currentSongName = url.substring(0,url.length()-4);
		}
		catch(Exception e)
		{
	         System.err.println(e.getMessage());
		}
	}
	
	public static void playNextSong()
	{
		String nextSong = "";
		//getting the next song
		for(int i = 0; i < songs.length; i++)
		{
			if(Sound.currentSongFileName.equals(songs[i]))
			{
				if(i==songs.length-1)
					nextSong = songs[0];
				else
					nextSong = songs[i+1];
			}
		}
		if(nextSong.equals(""))
		{
			return;
		}
		//stopping current song and playing the next one.
		System.out.println("Now Playing: "+nextSong.substring(0,nextSong.length()-4));
		stopMusic();
		playMusic(nextSong);
	}
	
	private static void fixMusicVolume()
	{
		if(Sound.musicMuted && Sound.musicVolume > 0)
		{
			Sound.musicMuted = false;
			Sound.muteMusic();
		}
	}
	
	public static void stopMusic()
	{
		music.stop();
	}
}