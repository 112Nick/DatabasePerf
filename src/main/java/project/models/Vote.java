package project.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Vote {
    private String nickname;
    private  int voice;

    public Vote() {
        this.nickname = "";
        this.voice = 0;
    }

    @JsonCreator
    public Vote(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("voice") int voice

    ) {
        this.nickname = nickname;
        this.voice = voice;

    }
    public int getVoice() {
        return voice;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }
}
