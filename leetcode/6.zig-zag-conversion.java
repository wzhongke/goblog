/*
 * @lc app=leetcode id=6 lang=java
 *
 * [6] ZigZag Conversion
 */

// @lc code=start
class Solution {
    public String convert(String s, int numRows) {
       //如果是有一行，可以直接返回
       if (numRows == 1) {
            return s;
        }
        StringBuilder result = new StringBuilder();
        //运用数学规律，逐行取值
        for (int i = 0; i < numRows; i++) {
            int j = 0;//表示第i行的第j个数
            while (j + i < s.length()) {
                result.append(s.charAt(j + i));//j每次循环时，首先取j+i坐标上的字母
                //如果不是第一排或者最后一排，一般情况下，每两个竖排间会有两个字母，第二个字母的规律是j+numRows * 2 - 2 - i
                if (i != 0 && i != numRows - 1 && j + numRows * 2 - 2 - i < s.length()) {
                    result.append(s.charAt(j + numRows * 2 - 2 - i));
                }
                //第一竖排和第二竖排的坐标差值为numRows * 2 - 2
                j += numRows * 2 - 2;
            }
        }
        return result.toString();
    }
}
// @lc code=end

