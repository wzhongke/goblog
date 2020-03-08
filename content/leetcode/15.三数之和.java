import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * @lc app=leetcode.cn id=15 lang=java
 *
 * [15] 三数之和
 */

// @lc code=start
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 3) {
            return result;
        }
        int len = nums.length;
        Arrays.sort(nums);
        if (nums[0] <= 0 && nums[len - 1] >= 0) {
            for(int i=0; i<len-2;) {
                if (nums[i] > 0) break;
                for (int first = i + 1, last = len - 1; first < last;) {
                    int sum = nums[i] + nums[first] + nums[last];
                    if (sum == 0) {
                        result.add(new ArrayList<>(Arrays.asList(nums[i], nums[first], nums[last])));
                    }
                    if (sum <= 0) {
                        while (first < last && nums[first] == nums[++first]){} // 如果相等就跳过
                    } else {
                        while (first < last && nums[last] == nums[--last]){} // 如果相等就跳过
                    }
                }
                while ( i+1<len && nums[i] == nums[++i]) {}
            }
        }
        return result;
    }
}
// @lc code=end

