<template>
  <div ref="root" class="viewer"></div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from "vue";
import Editor from "@toast-ui/editor";
import "@toast-ui/editor/dist/toastui-editor-viewer.css";

type Props = {
  content: string;
};

const props = defineProps<Props>();
const root = ref<HTMLDivElement | null>(null);
let viewer: { setMarkdown?: (val: string) => void; destroy?: () => void } | null = null;

onMounted(() => {
  if (!root.value) {
    return;
  }
  viewer = Editor.factory({
    el: root.value,
    viewer: true,
    initialValue: props.content ?? "",
    usageStatistics: false
  }) as typeof viewer;
});

watch(
  () => props.content,
  (val) => {
    if (viewer?.setMarkdown) {
      viewer.setMarkdown(val ?? "");
    }
  }
);

onBeforeUnmount(() => {
  if (viewer?.destroy) {
    viewer.destroy();
  }
  viewer = null;
});
</script>
