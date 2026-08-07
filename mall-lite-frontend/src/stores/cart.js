import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCartList, addCartItem } from '@/api/cart'
import { useMemberStore } from './member'

const STORAGE_KEY = 'mall_lite_cart_count'

// 购物车数量：本地优先（离线可用），并尽力与后端同步
export const useCartStore = defineStore('cart', () => {
  const count = ref(Number(localStorage.getItem(STORAGE_KEY) || 0))

  function setCount(n) {
    count.value = Math.max(0, Number(n) || 0)
    localStorage.setItem(STORAGE_KEY, String(count.value))
  }

  function fetchCount() {
    const memberStore = useMemberStore()
    if (!memberStore.isLoggedIn) return Promise.resolve()
    return getCartList()
      .then((res) => {
        const data = res?.data || {}
        const list = data.list || data || []
        const n = Array.isArray(list) ? list.length : Number(list) || count.value
        setCount(n)
      })
      .catch(() => {})
  }

  function addItem(product, qty = 1) {
    if (product?.id) {
      addCartItem({ productId: product.id, quantity: qty })
        .then(fetchCount)
        .catch(() => {})
    }
    setCount(count.value + qty)
    return count.value
  }

  return { count, setCount, fetchCount, addItem }
})
