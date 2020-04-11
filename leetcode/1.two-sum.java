import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=1 lang=java
 *
 * [1] Two Sum
 */

// @lc code=start
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int idx = 0; idx < nums.length; idx++) {
            if(map.containsKey(target - nums[idx])) {
                return new int[] {idx, map.get(target - nums[idx])};
            } else {
                map.put(nums[idx], idx);
            }
        }
        return null;
    }
}
// @lc code=end

