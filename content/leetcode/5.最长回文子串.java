/*
 * @lc app=leetcode.cn id=5 lang=java
 *
 * [5] 最长回文子串
 */

// @lc code=start
class Solution {

    public String longestPalindrome(String s) {
        return expandCenter(s);
    }
     /**
	 * 动态规划法
	 *  公式如下：
	 *      P(i,j) = true, (i,j) 是回文串
	 *      P(i,j) = false, (i,j) 不是回文串
	 *  初始条件:
	 *      P(i, i) = true
	 *      P(i, i+1) = S[i] == S[i+1]
	 *  递推：
	 *      P(i,j) = P(i+1, j-1) && S[i] == S[j]
	 *  我们首先初始化一字母和二字母的回文，然后找到所有三字母回文，并依此类推
	 */
    public String dynamic(String s) {
        if (s == null || s.length() == 0) {
			return "";
		}
		int length = s.length();
		boolean [][] p = new boolean[length][length];
		// P(i,j) = true
		for (int i=0; i<length - 1; i++) {
			p[i][i] = true;
			p[i][i+1] = s.charAt(i) == s.charAt(i+1);
		}
		int start =0, end = 0, max=0;
		
        for (int j=2; j<length; j++) {
			for (int i=0, l=j; l<length; i++, l++) {
				p[i][l] = p[i+1][l-1] && s.charAt(i) == s.charAt(l);
			}
		}

		for (int i=0; i<length; i++) {
			for (int j=i+1; j<length; j++) {
				if (p[i][j] && max < j - i + 1) {
					max = j - i + 1;
					start = i;
					end = j;
				}
			}
		}
		return s.substring(start, end  + 1);
    }

    /**
     * 中心扩展
     */
    String expandCenter(String s) {
		if (s == null || s.length() < 1) {
			return "";
		}
		int start = 0, end = 0;
		for (int i = 0; i < s.length(); i++) {
			int len1 = expandAroundCenter(s, i, i);
			int len2 = expandAroundCenter(s, i, i + 1);
			int len = Math.max(len1, len2);
			if (len > end - start) {
				start = i - (len - 1) / 2;
				end = i + len / 2;
			}
		}
		return s.substring(start, end + 1);
	}

	private int expandAroundCenter(String s, int left, int right) {
		while (left >= 0&& right < s.length() && s.charAt(left) == s.charAt(right)) {
			left--;
			right ++;
		}
		return right - left - 1;
	}
}
// @lc code=end

