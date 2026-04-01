<template>
  <div ref="root"></div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from "vue";
import Editor from "@toast-ui/editor";
import "@toast-ui/editor/dist/toastui-editor.css";

type EditType = "markdown" | "wysiwyg";

type Props = {
  modelValue: string;
  height?: string;
  previewStyle?: "vertical" | "tab";
  initialEditType?: EditType;
};

const props = defineProps<Props>();
const emit = defineEmits<{ (e: "update:modelValue", v: string): void }>();

const root = ref<HTMLDivElement | null>(null);
let editor: Editor | null = null;
let internalUpdate = false;

onMounted(() => {
  if (!root.value) {
    return;
  }
  editor = new Editor({
    el: root.value,
    height: props.height ?? "300px",
    initialEditType: props.initialEditType ?? "markdown",
    previewStyle: props.previewStyle ?? "vertical",
    initialValue: props.modelValue ?? "",
    usageStatistics: false
  });

  editor.on("change", () => {
    if (!editor) {
      return;
    }
    internalUpdate = true;
    emit("update:modelValue", editor.getMarkdown());
    internalUpdate = false;
  });
});

watch(
  () => props.modelValue,
  (val) => {
    if (!editor || internalUpdate) {
      return;
    }
    const next = val ?? "";
    if (editor.getMarkdown() !== next) {
      editor.setMarkdown(next);
    }
  }
);

onBeforeUnmount(() => {
  if (editor) {
    editor.destroy();
    editor = null;
  }
});
</script>
