package com.ericlam.qqbot.valbot.dto;

import com.mikuac.shiro.enums.ActionPath;

public record CustomRequest(String path) implements ActionPath {

    @Override
    public String getPath() {
        return path;
    }

}
