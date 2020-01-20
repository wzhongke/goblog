/*
 * @lc app=leetcode.cn id=12 lang=java
 *
 * [12] 整数转罗马数字
 */

// @lc code=start
class Solution {
    public String intToRoman(int num) {
		if (num < 1 || num > 3999) {
			return "";
		}
		char [][] intArr = new char[][] {{'I', 'V', 'X'}, {'X', 'L', 'C'}, {'C', 'D', 'M'}, {'M', 'M', 'M'}};
		StringBuilder preResult = new StringBuilder();
		for(int i=0; num > 0; i++, num /= 10) {
			int sub = num % 10;
			StringBuilder result = new StringBuilder();
			if (sub < 4) {
				while (sub > 0) {
					result.append(intArr[i][0]);
					sub --;
				}
			} else if (sub == 4) {
				result.append(intArr[i][0]).append(intArr[i][1]);
			} else if (sub == 5) {
				result.append(intArr[i][1]);
			} else if (sub == 9) {
				result.append(intArr[i][0]).append( intArr[i][2]);
			} else {
				result.append(intArr[i][1]);
				while (sub > 5) {
					result.append(intArr[i][0]);
					sub --;
				}
			}
			preResult.insert(0, result.toString());
		}
		return preResult.toString();
	}
}
// @lc code=end

