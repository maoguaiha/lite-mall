import {
  GoodsFilled, Monitor, ShoppingBag, Brush, Basketball, Reading,
  HomeFilled, Food, Present, Notebook, Van
} from '@element-plus/icons-vue'
import { markRaw } from 'vue'

// 分类 id -> Element Plus 图标组件。
// 后端分类 id 说明：1 数码 / 2 电脑 / 3 服饰 / 4 美妆 / 5 运动 / 6 图书
// / 26 家居 / 27 食品 / 34 母婴 / 35 玩具 / 36 汽车 / 37 文具
// markRaw 防止组件对象被放进响应式数组（如 ref(categories)）时被 reactive 化，
// 否则 Vue 会报警告：Vue received a Component that was made a reactive object。
const ICON_MAP = {
  1: markRaw(GoodsFilled),
  2: markRaw(Monitor),
  3: markRaw(ShoppingBag),
  4: markRaw(Brush),
  5: markRaw(Basketball),
  6: markRaw(Reading),
  26: markRaw(HomeFilled),
  27: markRaw(Food),
  34: markRaw(GoodsFilled),
  35: markRaw(Present),
  36: markRaw(Van),
  37: markRaw(Notebook)
}

export function iconForCategory(id) {
  return ICON_MAP[id] || markRaw(GoodsFilled)
}
