/*
 * @lc app=leetcode.cn id=44 lang=java
 *
 * [44] 通配符匹配
 */

// @lc code=start
class Solution {
    public boolean isMatch(String s, String p) {
        return dp(s, p);
    }

    public boolean dp (String s, String p) {
        int sLen = s.length(), pLen = p.length();

        // base cases
        if (p.equals(s) || p.equals("*")) return true;
        if (p.isEmpty() || s.isEmpty()) return false;
        boolean[][] d = new boolean[pLen + 1][sLen + 1];
        d[0][0] = true;

        for (int pIdx = 1; pIdx <= pLen; pIdx ++) {
            char curr = p.charAt(pIdx - 1);
            if (curr == '*') {
                int sIdx = 1;
                // d[p_idx - 1][s_idx - 1] is a string-pattern match
                // on the previous step, i.e. one character before.
                // Find the first idx in string with the previous math.
                while ((!d[pIdx - 1][sIdx - 1]) && (sIdx < sLen + 1)) sIdx++;
                // If (string) matches (pattern),
                // when (string) matches (pattern)* as well
                d[pIdx][sIdx - 1] = d[pIdx - 1][sIdx - 1];
                while (sIdx < sLen + 1) d[pIdx][sIdx++] = true;
            } else if (curr == '?') {
                for(int sIdx = 1; sIdx < sLen + 1; sIdx++) {
                    d[pIdx][sIdx] = d[pIdx - 1][sIdx - 1];
                }
            } else {
                for(int sIdx = 1; sIdx < sLen + 1; sIdx++) {
                    // Match is possible if there is a previous match
                    // and current characters are the same
                    d[pIdx][sIdx] = d[pIdx - 1][sIdx - 1] &&
                            (curr == s.charAt(sIdx - 1));
                }
            }
        }
        return d[pLen][sLen];
    }

    public boolean backtrack(String s, String p) {
        if (s.equals(p)) {
            return true;
        } else if (p.length() > 0 && s.length() == 0){
            for (int i=0; i<p.length(); i++) {
                if (p.charAt(i) != '*') {
                    return false;
                }
            }
            return true;
        }

        if (p.length() == 0) {
            return false;
        }
        String firstP = p.substring(0, 1);
        if (firstP.equals("?")) {
            return backtrack(s.substring(1), p.substring(1));
        } else if (firstP.equals("*")) {
            if (backtrack(s, p.substring(1))) {
                return true;
            } else {
                return backtrack(s.substring(1), p);
            }
        } else {
            String sub = s.substring(0, 1);
            if (sub.equals(firstP)) {
                return backtrack(s.substring(1), p.substring(1));
            } else {
                return false;
            }
        }
    }
}
// @lc code=end

