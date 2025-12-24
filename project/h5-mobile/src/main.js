import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'

// 引入Vant组件
import {
  Button,
  Form,
  Field,
  CellGroup,
  Cell,
  NavBar,
  Tabbar,
  TabbarItem,
  Card,
  Empty,
  List,
  PullRefresh,
  Tag,
  Icon,
  Image as VanImage,
  Uploader,
  ActionSheet,
  Dialog,
  Toast,
  Loading,
  Overlay,
  Popup,
  DatePicker,
  Col,
  Row,
  Search,
  Calendar,
  Picker
} from 'vant'

// 引入Vant样式
import 'vant/lib/index.css'

// 引入全局样式（放在Vant之后，确保覆盖Vant样式）
import './assets/styles/index.css'

const app = createApp(App)
const pinia = createPinia()

// 注册Vant组件
app.use(Button)
app.use(Form)
app.use(Field)
app.use(CellGroup)
app.use(Cell)
app.use(NavBar)
app.use(Tabbar)
app.use(TabbarItem)
app.use(Card)
app.use(Empty)
app.use(List)
app.use(PullRefresh)
app.use(Tag)
app.use(Icon)
app.use(VanImage)
app.use(Uploader)
app.use(ActionSheet)
app.use(Dialog)
app.use(Toast)
app.use(Loading)
app.use(Overlay)
app.use(Popup)
app.use(DatePicker)
app.use(Col)
app.use(Row)
app.use(Search)
app.use(Calendar)
app.use(Picker)

app.use(pinia)
app.use(router)

app.mount('#app')

