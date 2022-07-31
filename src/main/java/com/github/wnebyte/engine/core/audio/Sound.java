package com.github.wnebyte.engine.core.audio;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

@SuppressWarnings("resource")
public class Sound {

    private int bufferId;

    private int srcId;

    private String filepath;

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loops) {
        this.filepath = filepath;

        // Allocate space to store the return info from stb
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer =
                stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);
        if (rawAudioBuffer == null) {
            System.out.printf("(Debug): Failed to load sound: '%s'%n", filepath);
            stackPop();
            return;
        }

        // Retrieve the extra information
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        // Free
        stackPop();
        stackPop();

        // Find the correct OpenAL format
        int format = -1;
        switch (channels) {
            case 1:
                format = AL_FORMAT_MONO16;
                break;
            case 2:
                format = AL_FORMAT_STEREO16;
                break;
        }

        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        // Generate the source
        srcId = alGenSources();
        alSourcei(srcId, AL_BUFFER, bufferId);
        alSourcei(srcId, AL_LOOPING, loops ? 1 : 0);
        alSourcei(srcId, AL_POSITION, 0);
        alSourcef(srcId, AL_GAIN, 0.3f);

        // Free stb raw audio buffer
        free(rawAudioBuffer);
    }

    public void destroy() {
        alDeleteSources(srcId);
        alDeleteBuffers(bufferId);
    }

    public void play() {
        int state = alGetSourcei(srcId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
            // reset cursor
            alSourcei(srcId, AL_POSITION, 0);
        }

        if (!isPlaying) {
            alSourcePlay(srcId);
            isPlaying = true;
        }
    }

    public void stop() {
        if (isPlaying) {
            alSourceStop(srcId);
            isPlaying = false;
        }
    }

    public String getPath() {
        return filepath;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(srcId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
        }
        return isPlaying;
    }
}
