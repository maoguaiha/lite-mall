const api = require('../../utils/api')
const { formatImage } = require('../../utils/util')

Page({
  data: {
    parents: [],
    activeId: null,
    children: []
  },

  onLoad() {
    this.loadParents()
  },

  loadParents() {
    wx.showLoading({ title: '加载中' })
    api.getCategoryList(0)
      .then((list) => {
        const parents = (list || []).map((c) => ({
          id: c.id,
          name: c.name
        }))
        this.setData({ parents })
        if (parents.length > 0) {
          this.selectParent(parents[0].id)
        }
      })
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  selectParent(id) {
    this.setData({ activeId: id })
    api.getCategoryList(id)
      .then((list) => {
        this.setData({
          children: (list || []).map((c) => ({
            id: c.id,
            name: c.name,
            img: formatImage(c.icon)
          }))
        })
      })
      .catch(() => this.setData({ children: [] }))
  },

  onTapParent(e) {
    this.selectParent(e.currentTarget.dataset.id)
  },

  goList(e) {
    const id = e.currentTarget.dataset.id
    const name = e.currentTarget.dataset.name
    wx.navigateTo({
      url: '/pages/product/list?categoryId=' + id + '&name=' + encodeURIComponent(name || '')
    })
  }
})
