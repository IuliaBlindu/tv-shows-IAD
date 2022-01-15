package com.iad.tvshowsiad.result;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Game implements Serializable {
    String title;
    String platform;
}
