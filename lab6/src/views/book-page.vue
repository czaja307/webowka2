<script setup lang="ts">
import {computed, ref} from 'vue'
import {useMutation, useQuery, useQueryClient} from "@tanstack/vue-query";
import {Button} from "@/components/ui/button";
import {ArrowLeftIcon, ArrowRightIcon} from "@radix-icons/vue";
import {Input} from "@/components/ui/input";
import {Card, CardContent} from "@/components/ui/card";
import {Checkbox} from "@/components/ui/checkbox";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'

interface Book {
  id?: string
  title: string
  isAvailable?: boolean
  author?: Author | null
  authorId: string
  pages: number
}

interface Author {
  id: string
  firstName: string
  lastName: string
  birthDate: string | Date
}

interface BookResponse {
  content: Book[]
  totalPages: number
}

interface AuthorResponse {
  content: Author[]
  totalPages: number
}



const queryClient = useQueryClient();

const page = ref(0);
const totalPages = ref(1);
const itemsPerPage = ref(3);
const editingId = ref(null);
const currentBook = ref<Book>({
  id: '',
  title: '',
  isAvailable: false,
  author: null,
  authorId: 0,
  pages: 0,
});

const queryKey = computed(() => {
  return ['books', page.value, itemsPerPage.value];
});

const fetchBooks = async ({ queryKey }: { queryKey: (string | number)[]
}) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/books?page=${page.value}&size=${itemsPerPage.value}`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json() as BookResponse;
  totalPages.value = data.totalPages;
  console.log(totalPages.value)
  return data;
};

const {data, error, isLoading, isError} = useQuery({
  queryKey: queryKey.value,
  queryFn: fetchBooks,
  keepPreviousData: true,
})

const books = computed(() => data.value ? data.value.content : [])

const fetchAuthors = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/authors`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return await response.json() as Promise<AuthorResponse>;
};

const {data: authorsData, error: authorsError, isLoading: authorsLoading} = useQuery({
  queryKey: ['authors'],
  queryFn: fetchAuthors,
  keepPreviousData: true,
})

const authors = computed(() => authorsData.value ? authorsData.value.content : [])

const deleteMutation = useMutation({
  mutationFn: async (id: string) => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/books/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return id;
  },
  onSuccess: (id) => {
    queryClient.invalidateQueries({queryKey: ['books']});
  },
});

const deleteBook = async (id: string) => {
  await deleteMutation.mutateAsync(id);
};

const updateMutation = useMutation({
  mutationFn: async ({method, id, updatedData}: { method: "PUT" | "POST", id: string | null, updatedData: Partial<Book> }) => {
    let endpoint = `${import.meta.env.VITE_API_URL}/books/${id}`;
    if (method === "POST") {
      console.log("kkkk")
      endpoint = `${import.meta.env.VITE_API_URL}/books`;
    }
    const response = await fetch(endpoint, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(updatedData),
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json() as Promise<Book>;
  },
  onSuccess: () => {
    queryClient.invalidateQueries({queryKey: ['books']});
  },
});

const nextPage = () => {
  if (page.value < totalPages.value - 1) {
    page.value++;
  }
  queryClient.invalidateQueries({queryKey: ['books']});
};

const prevPage = () => {
  if (page.value > 0) {
    page.value--;
  }
  queryClient.invalidateQueries({queryKey: ['books']});
};

const startEditing = (book: Book) => {
  editingId.value = book.id;
  currentBook.value = {
    id: book.id,
    title: book.title,
    isAvailable: book.isAvailable,
    authorId: book.authorId,
    pages: book.pages,
  };
};

const saveBook = async (isBeingAdded?: boolean) => {
    console.log("book_c", currentBook)
    await updateMutation.mutateAsync(
        {
          method: isBeingAdded ? "POST" : "PUT",
          id: editingId.value,
          updatedData: {
            title: currentBook.value.title,
            authorId: currentBook.value.authorId,
            pages: currentBook.value.pages,
          }
        });
    editingId.value = null;
    currentBook.value = {
      id: '',
      title: '',
      isAvailable: false,
      author: null,
      authorId: 0,
      pages: 0,
    };
};


const cancelEditing = () => {
  editingId.value = null;
  currentBook.value = {
    id: '',
    title: '',
    isAvailable: false,
    author: null,
    authorId: 0,
    pages: 0,
  };
};

</script>


<template>
  <div class="flex items-center h-screen flex-col gap-10 p-4">
    <div class="flex flex-row w-full - justify-between">
      <h1 class="font-bold text-xl">Books</h1>
      <Dialog>
        <DialogTrigger as-child>
          <Button>Add</Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add a new book</DialogTitle>
          </DialogHeader>
          <Card class="sm:p-4 p-1">
            <CardContent class="flex flex-col gap-4">
              <div class="justify-start flex flex-col">
                <label for="title" class="text-sm text-gray-600">Title</label>
                <Input
                    v-model="currentBook.title"
                    type="text"
                    id="title"
                />
              </div>
              <div class="justify-start flex flex-col">
                <label for="pages" class="text-sm text-gray-600">Pages</label>
                <Input
                    v-model="currentBook.pages"
                    type="number"
                    id="pages"
                />
              </div>
              <div class="justify-start flex flex-col">
                <label for="title" class="text-sm text-gray-600">Author</label>
                <div>
                  <select
                      v-model="currentBook.authorId"
                      id="authorId"
                      class="border border-gray-300 rounded-md p-2"
                  >
                    <option disabled value="">Select author</option>
                    <option v-for="author in authors" :key="author.id" :value="author.id">
                      {{ author.firstName }} {{ author.lastName }}
                    </option>
                  </select>
                </div>
                <Button
                    class="mt-2"
                    @click="saveBook(true)"
                >
                  Save
                </Button>
                <DialogClose as-child>
                  <Button
                      variant="outline"
                      class="mt-2"
                      @click="cancelEditing"
                  >
                    Cancel
                  </Button>
                </DialogClose>
              </div>
            </CardContent>
          </Card>
        </DialogContent>
      </Dialog>
    </div>

    <div v-if="isLoading || authorsLoading">
      Loading...
    </div>

    <div v-else-if="isError || authorsError">
      Error: {{ error.message }} {{ authorsError.message }}
    </div>

    <div v-else>
      <ul>
        <li v-for="book in books" :key="book.id">
          <div class="p-10">
            <Card class="sm:p-4 p-1">
              <CardContent class="flex flex-col gap-4">
                <div class="justify-start flex flex-col">
                  <label for="title" class="text-sm text-gray-600">Title</label>
                  <Input
                      v-if="editingId !== book.id"
                      v-model="book.title"
                      type="text"
                      id="title"
                      :disabled="editingId !== book.id"
                  />
                  <Input
                      v-if="editingId === book.id"
                      v-model="currentBook.title"
                      type="text"
                      id="title"
                      :disabled="editingId !== book.id"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="isAvailable" class="text-sm text-gray-600">Is available</label>
                  <Checkbox
                      v-model="book.isAvailable"
                      id="isAvailable"
                      :disabled="true"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="pages" class="text-sm text-gray-600">Pages</label>
                  <Input
                      v-if="editingId !== book.id"
                      v-model="book.pages"
                      type="number"
                      id="pages"
                      :disabled="editingId !== book.id"
                  />
                  <Input
                      v-if="editingId === book.id"
                      v-model="currentBook.pages"
                      type="number"
                      id="pages"
                      :disabled="editingId !== book.id"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="title" class="text-sm text-gray-600">Author</label>
                  <div v-if="editingId === book.id">
                    <select
                        v-model="currentBook.authorId"
                        id="authorId"
                        :disabled="editingId !== book.id"
                        class="border border-gray-300 rounded-md p-2"
                    >
                      <option disabled value="">Select author</option>
                      <option v-for="author in authors" :key="author.id" :value="author.id">
                        {{ author.firstName }} {{ author.lastName }}
                      </option>
                    </select>
                  </div>
                  <div v-else>
                    {{ book.author?.firstName }} {{ book.author?.lastName }}
                  </div>
                  <Button
                      v-if="editingId !== book.id"
                      class="mt-2"
                      @click="startEditing(book)"
                      >
                    Edit
                  </Button>
                  <Button
                      v-if="editingId !== book.id"
                      class="mt-2"
                      @click="deleteBook(book.id)"
                      variant="destructive"
                  >Delete</Button>
                  <Button
                      v-if="editingId === book.id"
                      class="mt-2"
                      @click="saveBook(false)"
                      >
                    Save
                  </Button>
                  <Button
                      v-if="editingId === book.id"
                      variant="outline"
                      class="mt-2"
                      @click="cancelEditing"
                      >
                    Cancel
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </li>
      </ul>

    </div>

    <div class="flex flex-row gap-4 items-center p-4">
      <Button variant="outline" @click="prevPage">
        <ArrowLeftIcon/>
      </Button>
      <p>
        {{ page + 1 }}
      </p>
      <Button variant="outline" @click="nextPage">
        <ArrowRightIcon/>
      </Button>
    </div>


  </div>

</template>

<style scoped>

</style>