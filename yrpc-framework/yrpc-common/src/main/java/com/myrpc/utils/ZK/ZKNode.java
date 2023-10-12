package com.myrpc.utils.ZK;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZKNode {
    private String nodePath;
    private byte[] data;
}
