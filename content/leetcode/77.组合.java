/*
 * @lc app=leetcode.cn id=77 lang=java
 *
 * [77] 组合
 */

// @lc code=start
class Solution {
    /**
     * 
     * 将 nums 初始化为从 1 到 k的整数序列。 将 n + 1添加为末尾元素，起到“哨兵”的作用。
     * 将指针设为列表的开头 j = 0.
     *
     * While j < k :
     *
     *   1. 将nums 中的前k个元素添加到输出中，换而言之，除了“哨兵”之外的全部元素。
     *   2. 找到nums中的第一个满足 nums[j] + 1 != nums[j + 1]的元素，并将其加一
     * nums[j]++ 以转到下一个组合。
     * @param n
     * @param k
     * @return
     */
    public List<List<Integer>> combine(int n, int k) {
        // init first combination
        LinkedList<Integer> nums = new LinkedList<Integer>();
        for(int i = 1; i < k + 1; ++i)
            nums.add(i);
        nums.add(n + 1);

        List<List<Integer>> output = new ArrayList<List<Integer>>();
        int j = 0;
        while (j < k) {
            // add current combination
            output.add(new LinkedList(nums.subList(0, k)));
            // increase first nums[j] by one
            // if nums[j] + 1 != nums[j + 1]
            j = 0;
            while ((j < k) && (nums.get(j + 1) == nums.get(j) + 1))
                nums.set(j, j++ + 1);
            nums.set(j, nums.get(j) + 1);
        }
        return output;
    }
}
// @lc code=end

