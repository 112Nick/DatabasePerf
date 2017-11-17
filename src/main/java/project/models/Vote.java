package project.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Vote {
    private String nickname;
    private int userID;
    private  int voice;

    public Vote() {
        this.nickname = "";
        this.voice = 0;
    }

    @JsonCreator
    public Vote(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("userID") int uid,
            @JsonProperty("voice") int voice

    ) {
        this.nickname = nickname;
        this.voice = voice;
        this.userID = uid;


    }
    public int getVoice() {
        return voice;
    }

    public int getUserID() {
        return userID;
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
