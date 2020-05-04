import java.util.ArrayList;
import java.util.LinkedList;

/*
 * @lc app=leetcode.cn id=78 lang=java
 *
 * [78] 子集
 */

// @lc code=start
class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        int len = nums.length;
        int max = (int) Math.pow(2, len);
        ArrayList<List<Integer>> result = new ArrayList<>();
        for(int i = 0; i < max; i++) {
            ArrayList<Integer> sub = new ArrayList<>();
            for (int j=0; j<len; j++) {
                int index = 1 << j;
                if (index > i) {
                    break;
                }
                if ((index & i) > 0) {
                    sub.add(nums[j]);
                }
            }
            result.add(sub);
        }
        return result;
    }
}
// @lc code=end

