import {
  GoodsFilled, Monitor, ShoppingBag, Brush, Basketball, Reading,
  HomeFilled, Food, Present, Notebook, Van
} from '@element-plus/icons-vue'

// 分类 id -> Element Plus 图标组件。
// 后端分类 id 说明：1 数码 / 2 电脑 / 3 服饰 / 4 美妆 / 5 运动 / 6 图书
// / 26 家居 / 27 食品 / 34 母婴 / 35 玩具 / 36 汽车 / 37 文具
const ICON_MAP = {
  1: GoodsFilled,
  2: Monitor,
  3: ShoppingBag,
  4: Brush,
  5: Basketball,
  6: Reading,
  26: HomeFilled,
  27: Food,
  34: GoodsFilled,
  35: Present,
  36: Van,
  37: Notebook
}

export function iconForCategory(id) {
  return ICON_MAP[id] || GoodsFilled
}
