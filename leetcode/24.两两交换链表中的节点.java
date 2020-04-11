/*
 * @lc app=leetcode.cn id=24 lang=java
 *
 * [24] 两两交换链表中的节点
 */

// @lc code=start
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode root = new ListNode(0);
        root.next = head;
        ListNode pre = root;
        ListNode second = head;
        ListNode suf = head.next;
        while (suf != null) {
            ListNode next = suf.next;
            pre.next = suf;
            suf.next = second;
            second.next = next;
            pre = second;
            second = pre.next;
            if (second == null ) break;
            suf = second.next;
        }
        return root.next;
    }
}
// @lc code=end

