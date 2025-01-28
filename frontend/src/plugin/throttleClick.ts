// src/plugins/throttleClick.ts
import type { App, DirectiveBinding } from 'vue'

// HTMLElement 타입 확장
interface ThrottleElement extends HTMLElement {
    _throttleClick?: (e: Event) => void
}

export const throttleClick = {
    install: (app: App) => {
        app.directive('throttle-click', {
            created: (el: ThrottleElement, binding: DirectiveBinding) => {
                let throttleTimeout: number | null = null

                const throttleHandler = (e: Event) => {
                    if (throttleTimeout === null) {
                        // 원래의 이벤트 핸들러 실행
                        binding.value(e)

                        // 버튼 비활성화
                        el.setAttribute('disabled', 'disabled')
                        if (el.style) {
                            el.style.opacity = '0.7'
                            el.style.cursor = 'not-allowed'
                            el.style.pointerEvents = 'none'
                        }

                        // 지정된 시간 후에 다시 활성화
                        throttleTimeout = window.setTimeout(() => {
                            throttleTimeout = null
                            el.removeAttribute('disabled')
                            if (el.style) {
                                el.style.opacity = ''
                                el.style.cursor = ''
                                el.style.pointerEvents = ''
                            }
                        }, binding.arg || 300)
                    } else {
                        e.stopPropagation()
                        e.preventDefault()
                    }
                }

                // 기존 이벤트 리스너 제거를 위해 저장
                el._throttleClick = throttleHandler

                // 이벤트 리스너 등록
                el.addEventListener('click', throttleHandler)
            },

            beforeUnmount: (el: ThrottleElement) => {
                // cleanup
                if (el._throttleClick) {
                    el.removeEventListener('click', el._throttleClick)
                    delete el._throttleClick
                }
            }
        })
    }
}