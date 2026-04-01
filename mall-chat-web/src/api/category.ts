import { BASE_URL, request } from "./http";

export type CategoryItem = {
    id: number;
    name: string;
    parentId?: number | null;
    level: number;
    sort: number;
};

export function fetchCategories() {
    return request<CategoryItem[]>(BASE_URL, "/categories");
}
