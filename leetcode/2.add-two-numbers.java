/*
 * @lc app=leetcode id=2 lang=java
 *
 * [2] Add Two Numbers
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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode resultNode = new ListNode(0);
		ListNode nextNode = resultNode;
		int exp = 0;
		while(l1 != null && l2 != null) {
			int sum = l1.val + l2.val + exp;
			nextNode.next = new ListNode(sum % 10);
			nextNode = nextNode.next;
			exp = sum / 10;
			l1 = l1.next;
			l2 = l2.next;
		}
		ListNode l = l1 == null ? l2 : l1;
		while(l != null) {
			nextNode.next = new ListNode(l.val + exp);
			nextNode = nextNode.next;
			l = l.next;
			exp = 0;
		}
		return resultNode.next;
    }
}
// @lc code=end

