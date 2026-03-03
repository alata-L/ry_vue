import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

/** 首页/index/index2 仅 admin、manger 可访问；普通角色(common)无权限时需重定向到其第一个菜单 */
const homePaths = ['/', '/index', '/index2']
const canAccessHome = (roles) => roles && (roles.includes('admin') || roles.includes('manger'))

/** 从侧栏路由中取第一个可访问的完整路径（用于普通角色默认跳转） */
function getFirstMenuPath(routes, basePath = '') {
  if (!routes || !routes.length) return null
  for (const r of routes) {
    if (r.hidden) continue
    const seg = (r.path || '').replace(/^\/+/, '')
    const pathStr = (basePath ? basePath.replace(/\/+$/, '') + '/' : '/') + seg
    const normalized = pathStr.replace(/\/+/g, '/') || '/'
    if (r.children && r.children.length) {
      const first = getFirstMenuPath(r.children, normalized)
      if (first) return first
    } else if (r.path !== undefined && r.path !== '') {
      return normalized
    }
  }
  return null
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes) // 动态添加可访问路由表
            const targetPath = to.path
            const isHomeTarget = targetPath === '/' || targetPath === '/index' || targetPath === '/index2'
            const roles = store.getters.roles || []
            // 普通角色无首页权限时，若登录后跳转目标是首页，则改跳到其第一个可访问菜单，避免 404
            if (isHomeTarget && !canAccessHome(roles)) {
              const first = getFirstMenuPath(store.getters.sidebarRouters)
              next(first ? { path: first, replace: true } : { ...to, replace: true })
            } else {
              next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
            }
          })
        }).catch(err => {
            store.dispatch('LogOut').then(() => {
              Message.error(err)
              next({ path: '/' })
            })
          })
      } else {
        // 普通角色不能访问首页/index/index2，重定向到其第一个可访问菜单
        if (homePaths.some(p => to.path === p || (p !== '/' && to.path.startsWith(p)))) {
          const roles = store.getters.roles || []
          if (!canAccessHome(roles)) {
            const first = getFirstMenuPath(store.getters.sidebarRouters)
            if (first) {
              next(first)
              return
            }
          }
        }
        next()
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
