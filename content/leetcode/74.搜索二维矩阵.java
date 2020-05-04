/*
 * @lc app=leetcode.cn id=74 lang=java
 *
 * [74] 搜索二维矩阵
 */

// @lc code=start
class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        int n = matrix.length;
        int m = matrix[0].length;
        int start = 0, end = m * n - 1;
        while (start <= end) {
            int  middle = (start + end ) / 2;
            int line = middle / m;
            int col = middle % m;
            if (matrix[line][col] == target) {
                return true;
            }
            if (matrix[line][col] < target) {
                start = middle + 1;
            } else {
                end = middle - 1;
            }
        }
        return false;
    }
}
// @lc code=end

