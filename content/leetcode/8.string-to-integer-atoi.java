/*
 * @lc app=leetcode id=8 lang=java
 *
 * [8] String to Integer (atoi)
 */

// @lc code=start
class Solution {
    public int myAtoi(String str) {
		int atoi = 0;
		boolean start = false;
		int nev = 1;
		for (byte b: str.getBytes()) {
			if (b == ' ' && !start) {
				continue;
			}
			if (b == '-' && !start) {
				nev	= -1;
				start = true;
			} else if (b == '+' && !start) {
				start = true;
			} else if (b >= '0' && b <= '9') {
				int mum = (b - '0') * nev;
				if (nev > 0 && (atoi > Integer.MAX_VALUE / 10 || (atoi == Integer.MAX_VALUE / 10 && mum >= 7))) {
					return Integer.MAX_VALUE;
				} else if (nev < 0 && atoi < Integer.MIN_VALUE / 10 || (atoi == Integer.MIN_VALUE / 10 && mum <= -8)) {
					return Integer.MIN_VALUE;
				}
				atoi = atoi * 10 + mum;
				start = true;
			} else {
				break;
			}
		}
		return atoi;
    }
}
// @lc code=end

