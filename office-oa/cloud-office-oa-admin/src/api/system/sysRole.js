/*
角色管理相关的API请求函数
*/
import request from '@/utils/request'

const api_name = '/admin/system/sysRole'

export default {

  /*
  获取角色分页列表(带搜索)
  */
  getPageList(currentPage, limit, searchObj) {
    return request({
      url: `${api_name}/${currentPage}/${limit}`,
      method: 'get',
      params: searchObj
    })
  }
}
