public interface DES {

    static public String decode(String key, String data) {
        String Decoded = "";
        String Key[] = setKey(key, 1);//密钥生成
//        data = Tools.toBinary(data);//��������ת��Ϊ������
        int l = data.length(), n = l / 64;
        String temp[] = new String[n];
        if (l > 64) {
            temp = dataSplite(data, n);
        } else {
            temp[0] = data;
        }
        //          System.out.println("data:"+data);
        for (int i = 0; i < n; i++) {
            temp[i] = Permute(temp[i], matrix_1);//��ʼ�û�
            temp[i] = F(temp[i], Key);
            temp[i] = Permute(temp[i], matrix_2);//���û�
//            Decoded += Tools.toString(temp[i]);
            Decoded += toString(temp[i]);
        }
        return Decoded;
    }

    static public String encode(String key, String data) {
        String Encoded = "";
        data = toBinary(data);//输入明文转化为二进制
        data = fill(data);
        String Key[] = setKey(key, 0);//密钥生成
        int n, l = data.length();
        if (l % 64 == 0) {
            n = l / 64;
        } else {
            n = (l / 64) + 1;
        }
        String temp[] = new String[n];
        if (n == 1) {
            temp[0] = data;
        } else {
            temp = dataSplite(data, n);
        }
        for (int i = 0; i < n; i++) {
            String s;
            s = Permute(temp[i], matrix_1);//初始置换
            s = F(s, Key);
            s = Permute(s, matrix_2);//逆置换
            //Encoded += toString(s);
            Encoded += s;
        }
        System.out.println("encoded:" + temp);
        return Encoded;
    }


    static String[] setKey(String key, int ed) {
        String Key[] = new String[16];
        String binKey = toBinary(key);
//      String binKey=key;
        binKey = Permute(binKey, pc1);
        int r[] = round;
        String c[] = new String[17], d[] = new String[17];
        String temp;
        c[0] = binKey.substring(0, 28);
        d[0] = binKey.substring(28);
        if (ed == 0) {
            for (int i = 0; i < 16; i++) {
                c[i + 1] = move(c[i], r[i]);
                d[i + 1] = move(d[i], r[i]);
                temp = c[i + 1] + d[i + 1];
                Key[i] = Permute(temp, pc2);
            }
        } else {
            for (int i = 0; i < 16; i++) {
                c[i + 1] = move(c[i], r[i]);
                d[i + 1] = move(d[i], r[i]);
                temp = c[i + 1] + d[i + 1];
                Key[15 - i] = Permute(temp, pc2);
            }
        }
        return Key;
    }

    //异或
    static String xor(String a, String b) {
        int length = a.length();
        String result = "";
        for (int i = 0; i < length; i++) {
            result = result + (a.charAt(i) ^ b.charAt(i));
        }
        return result;
    }

    //补位
    static String fill(String data) {
        String newdata = data;
        int n = newdata.length() % 64;
        if (n != 0) {
            for (int i = 0; i < (64 - n); i++) {
                newdata = "0" + newdata;
            }
        }
        return newdata;
    }

    //F函数实现
    static String F(String data, String Key[]) {
        String left[] = new String[17];
        String right[] = new String[17];
        left[0] = data.substring(0, 32);
        right[0] = data.substring(32, 64);
        for (int i = 1; i <= 16; i++) {
            left[i] = right[i - 1];
            String temp;
            temp = Permute(right[i - 1], extend);//扩展变换
            temp = xor(temp, Key[i - 1]);//密钥异或
            temp = Compress(temp);//S盒压缩
            temp = Permute(temp, P);//P盒置换
            right[i] = xor(temp, left[i - 1]);//左右异或
            data = right[i] + left[i];//左右互换

        }
        return data;
    }

    //循环左移位
    static String move(String key, int num) {
        String moved;
        moved = key.substring(num) + key.substring(0, num);
        return moved;
    }

    //各种变换
    static String Permute(String input, int matrix[]) {//初始置换，逆置换，P盒置换
        String permuted = "";
        for (int i = 0; i < matrix.length; i++) {
            permuted = permuted + input.charAt(matrix[i] - 1);
        }
        //  System.out.println(permuted);
        return permuted;
    }

    //S盒压缩
    static String Compress(String encrypted) {
        int s[][];
        int row, line;
        String compressed = "", temp;
        for (int i = 0; i < 8; i++) {
            String r = "", l = "";
            r = r + encrypted.charAt(i * 6) + encrypted.charAt(i * 6 + 5);
            l = l + encrypted.substring(i * 6 + 1, i * 6 + 5);
            row = Integer.valueOf(r, 2);
            line = Integer.valueOf(l, 2);
            s = getS((i + 1));
            temp = String.format("%04d", Integer.parseInt(Integer.toBinaryString(s[row][line])));
            compressed += temp;
        }
        return compressed;
    }

    //信息的切割
    static String[] dataSplite(String longdata, int n) {
        String data[] = new String[n];
        for (int i = 0; i < n; i++) {
            if (i != n - 1) {
                data[i] = longdata.substring(i * 64, i * 64 + 64);
            } else {
                data[i] = fill(longdata.substring(i * 64));
            }
        }
        return data;

    }

    static String toBinary(String str) {
        //?????????????????
        char[] strChar = str.toCharArray();
        String result = "", temp;
        for (int i = 0; i < strChar.length; i++) {
            temp = String.format("%08d", Integer.parseInt(Integer.toBinaryString(strChar[i])));
            result += temp;
        }
        return result;
    }

    static String toString(String str) {
        String s = "";
        for (int j = 0; j < str.length() / 8; j++) {
            String t1 = str.substring(j * 8, j * 8 + 8);
            if (!t1.equals("00000000")) {
                int temp = Integer.valueOf(t1, 2);
                s = s + (char) temp;
            }
        }
        return s;

    }


    static int[][] getS(int s) {
        switch (s) {
            case 1: {
                return S1;
            }
            case 2: {
                return S2;
            }
            case 3: {
                return S3;
            }
            case 4: {
                return S4;
            }
            case 5: {
                return S5;
            }
            case 6: {
                return S6;
            }
            case 7: {
                return S7;
            }
            case 8: {
                return S8;
            }
        }
        return null;
    }

    int matrix_1[] = {58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7};


    int matrix_2[] = {40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25};

    int extend[] = {32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1};

    int S1[][] = {{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
            {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
            {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
            {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}};

    int S2[][] = {{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
            {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
            {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
            {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}};

    int S3[][] = {{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
            {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
            {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
            {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}};

    int S4[][] = {{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
            {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
            {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
            {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}};

    int S5[][] = {{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
            {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
            {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
            {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}};

    int S6[][] = {{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
            {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
            {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
            {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}};

    int S7[][] = {{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
            {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
            {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
            {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}};

    int S8[][] = {{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
            {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
            {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
            {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}};

    int P[] = {16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25};

    int pc1[] = {57, 49, 41, 33, 25, 17, 9, 1,
            58, 50, 42, 34, 26, 18, 10, 2,
            59, 51, 43, 35, 27, 19, 11, 3,
            60, 52, 44, 36, 63, 55, 47, 39,
            31, 23, 15, 7, 62, 54, 46, 38,
            30, 22, 14, 6, 61, 53, 45, 37,
            29, 21, 13, 5, 28, 20, 12, 4};
    int pc2[] = {14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32};

    int round[] = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

}