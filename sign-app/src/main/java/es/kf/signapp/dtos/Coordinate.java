package es.kf.signapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coordinate {
    private int x;
    private int y;
    private int width;
    private int height;
    private int pageIndex;
}
