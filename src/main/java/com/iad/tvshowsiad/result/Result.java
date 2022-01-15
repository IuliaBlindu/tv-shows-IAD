package com.iad.tvshowsiad.result;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    String title;
    String platform;
}
