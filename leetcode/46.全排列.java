import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * @lc app=leetcode.cn id=46 lang=java
 *
 * [46] 全排列
 */

// @lc code=start
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        ArrayList<Integer> numList = new ArrayList<>();
        for (int num : nums)
            numList.add(num);
        List<List<Integer>> res = new ArrayList<>();
        backtrack(nums.length, numList, res, 0);
        return res;
    }

    private void backtrack(int len, ArrayList<Integer> nums, List<List<Integer>> res, int first) {
        if (first == len) {
            res.add(new ArrayList<>(nums));
        }
        for (int i=first; i<len; i++) {
            Collections.swap(nums, first, i);
            // use next integers to complete the permutations
            backtrack(len, nums, res, first + 1);
            // backtrack
            Collections.swap(nums, first, i);
        }
    }
}
// @lc code=end

