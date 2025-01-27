import { defineStore } from 'pinia'

export const useDropdownStore = defineStore('dropdown', {
    state: () => ({
        openDropdown: null as string | null
    }),
    actions: {
        setOpenDropdown(dropdown: string | null) {
            this.openDropdown = dropdown
        }
    }
})